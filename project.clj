(defproject nomnom/duckula.monitoring "0.5.0"
  :description "Monitoring Component for Duckula"
  :url "https://github.com/nomnom-insights/nomnom.duckula.monitoring"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2018
            :key "mit"}
  :deploy-repositories {"clojars" {:sign-releases false
                                   :username [:gpg :env/clojars_username]
                                   :password [:gpg :env/clojars_password]}}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.stuartsierra/component "0.4.0"]
                 [org.clojure/tools.logging "0.5.0"]
                 [nomnom/duckula "0.5.0"]
                 [nomnom/stature "2.0.0"]
                 [nomnom/caliban "1.0.2"]]
    :profiles {:dev
             {:dependencies  [[ch.qos.logback/logback-classic "1.2.3"]]}})
