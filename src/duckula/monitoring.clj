(ns duckula.monitoring
  (:require [duckula.protocol]
            [clojure.tools.logging :as log]
            [caliban.tracker.protocol :as tracker]
            [stature.metrics.protocol :as stature]
            [com.stuartsierra.component :as component]))

(defrecord Monitoring [name exception-tracker statsd]
  component/Lifecycle
  (start [this]
    (log/infof "monitoring=%s start" name)
    this)
  (stop [this]
    (log/warnf "monitoring=%s stop" name)
    this)
  duckula.protocol/Monitoring
  (record-timing [this key time-ms]
    (log/infof "request=%s time=%s" key time-ms)
    (stature/timing statsd key time-ms))
  (on-success [this key response]
    (log/infof "request=%s status=success:%s" key (:status response))
    (stature/count statsd key))
  (on-error [this key]
    (log/warnf "request=%s status=error" key)
    (stature/count statsd key))
  (on-failure [this key]
    (log/errorf "request=%s status=failure" key)
    (stature/count statsd key))
  (on-not-found [this key uri]
    (log/warnf "request=%s status=not-found uri=%s" key uri)
    (stature/count statsd key))
  (track-exception [this exception] (log/error exception)
    (tracker/report exception-tracker exception))
  (track-exception [this exception data]
    (log/errorf exception "data=%s" data)
    (tracker/report exception-tracker exception data)))

(defn create [{:keys [name]}]
  (map->Monitoring {:name (or name "duckula.monitoring")}))
