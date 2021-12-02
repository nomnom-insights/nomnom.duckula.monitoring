(defproject nomnom/duckula.monitoring "0.6.1-SNAPSHOT-1"
  :description "Monitoring Component for Duckula"
  :url "https://github.com/nomnom-insights/nomnom.duckula.monitoring"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2018
            :key "mit"}

  :deploy-repositories [["releases"  {:sign-releases false :url "https://clojars.org"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org"}]]

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.stuartsierra/component "1.0.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [nomnom/duckula "0.5.0"]
                 [nomnom/stature "2.0.0"]
                 [nomnom/caliban "1.0.2"]]

  :profiles {:dev
             {:dependencies  [[ch.qos.logback/logback-classic "1.2.7"]]}})
