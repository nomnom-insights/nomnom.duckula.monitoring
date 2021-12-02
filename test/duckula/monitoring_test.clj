(ns duckula.monitoring-test
  (:require
    [caliban.tracker.mock :as tracker.mock]
    [clojure.test :refer [deftest is use-fixtures]]
    [com.stuartsierra.component :as component]
    [duckula.monitoring :as monitoring]
    [duckula.protocol :as protocol]
    [stature.metrics.protocol :as metrics]))


(def counter-state (atom {}))


(defrecord FakeStatsd
  []

  component/Lifecycle

  (start [this] this)


  (stop
    [this]
    (reset! counter-state {})
    this)

  metrics/Metrics

  (count
    [_this key]
    (swap! counter-state (fn [c] (update c key #(inc (get % key 0))))))


  (timing
    [_this key val]
    (swap! counter-state #(assoc % (str key ".ms") val))))


(use-fixtures :each (fn [test-fn]
                      (reset! counter-state {})
                      (test-fn)))


(deftest monitoring-test
  (let [system (component/start-system
                 {:exception-tracker (tracker.mock/create)
                  :statsd (->FakeStatsd)
                  :monitoring (component/using
                                (monitoring/create {:name "no name"})
                                [:statsd :exception-tracker])})]
    (protocol/record-timing (:monitoring system) "test.timing" 100)
    (protocol/on-success (:monitoring system) "test.success" {:status 210})
    (protocol/on-error (:monitoring system) "test.error")
    (protocol/on-failure (:monitoring system) "test.failure")
    (protocol/on-failure (:monitoring system) "test.failure")
    (protocol/on-not-found (:monitoring system) "test.not-found" "/not-found-yo")
    (try
      (throw (ex-info "boo" {}))
      (catch Exception err
        (protocol/track-exception (:monitoring system) err)))
    (is (= {"test.error" 1
            "test.failure" 1
            "test.not-found" 1
            "test.success" 1
            "test.timing.ms" 100}
           @counter-state))
    (component/stop system)))


(deftest exclusion-test
  (let [system (component/start-system
                 {:exception-tracker (tracker.mock/create)
                  :statsd (->FakeStatsd)
                  :monitoring (component/using
                                (monitoring/create {:name "no name" :exclude-pattern #".*health-check"})
                                [:statsd :exception-tracker])})]
    (protocol/record-timing (:monitoring system) "test.timing" 100)
    (protocol/on-success (:monitoring system) "test.success" {:status 210})
    (protocol/on-success (:monitoring system) "health.success" {:status 210})
    (protocol/on-success (:monitoring system) "health-check.success" {:status 210})
    (protocol/on-error (:monitoring system) "test.error")
    (protocol/on-failure (:monitoring system) "test.failure")
    (protocol/on-failure (:monitoring system) "test.failure")
    (protocol/on-not-found (:monitoring system) "test.not-found" "/not-found-yo")
    (try
      (throw (ex-info "boo" {}))
      (catch Exception err
        (protocol/track-exception (:monitoring system) err)))
    (is (= {"test.error" 1
            "test.failure" 1
            "test.not-found" 1
            "test.success" 1
            "health.success" 1
            "test.timing.ms" 100}
           @counter-state))
    (component/stop system)))
