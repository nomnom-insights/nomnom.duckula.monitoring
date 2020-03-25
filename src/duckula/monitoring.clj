(ns duckula.monitoring
  (:require
    [caliban.tracker.protocol :as tracker]
    [clojure.tools.logging :as log]
    [com.stuartsierra.component :as component]
    [duckula.protocol]
    [stature.metrics.protocol :as stature])
  (:import
    (java.util.regex
      Pattern)))


(defn should-report?
  [exclude-pattern key]
  (not (and exclude-pattern (re-find exclude-pattern key))))


(defrecord Monitoring
  [name exclude-pattern exception-tracker statsd]

  component/Lifecycle
  (start [this]
    (log/infof "monitoring=%s start" name)
    this)
  (stop [this]
    (log/warnf "monitoring=%s stop" name)
    this)


  duckula.protocol/Monitoring

  (record-timing
    [this key time-ms]
    (if (should-report? exclude-pattern key)
      (do
        (log/infof "request=%s time=%s" key time-ms)
        (stature/timing statsd key time-ms))
      (log/debugf "request=%s time=%s" key time-ms)))


  (on-success
    [this key response]
    (if (should-report? exclude-pattern key)
      (do
        (log/infof "request=%s status=success:%s" key (:status response))
        (stature/count statsd key))
      (log/debugf "request=%s status=success:%s" key (:status response))))


  (on-error
    [this key]
    (if (should-report? exclude-pattern key)
      (do
        (log/warnf "request=%s status=error" key)
        (stature/count statsd key))
      (log/debugf "request=%s status=error" key)))

  (on-failure
    [this key]
    (if (should-report? exclude-pattern key)
      (do
        (log/errorf "request=%s status=failure" key)
        (stature/count statsd key))
      (log/debugf "request=%s status=failure" key)))

  (on-not-found
    [this key uri]
    (if (should-report? exclude-pattern key)
      (do
        (log/warnf "request=%s status=not-found uri=%s" key uri)
        (stature/count statsd key))
      (log/warnf "request=%s status=not-found uri=%s" key uri)))

  (track-exception
    [this exception]
    (log/error exception)
    (tracker/report exception-tracker exception))

  (track-exception
    [this exception data]
    (log/errorf exception "data=%s" data)
    (tracker/report exception-tracker exception data)))


(defn create
  [{:keys [name exclude-pattern]}]
  {:pre [(or (nil? exclude-pattern)
             (instance? Pattern exclude-pattern))]}
  (map->Monitoring {:exclude-pattern exclude-pattern
                    :name (or name "duckula.monitoring")}))
