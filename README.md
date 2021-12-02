# duckula.monitoring

[![CircleCI](https://circleci.com/gh/nomnom-insights/nomnom.duckula.monitoring.svg?style=svg)](https://circleci.com/gh/nomnom-insights/nomnom.duckula.monitoring)

[![Clojars Project](https://img.shields.io/clojars/v/nomnom/duckula.monitoring.svg)](https://clojars.org/nomnom/duckula.monitoring)

A fully functioning monitoring component for [Duckula](https://github.com/nomnom-insights/nomnom.duckula).

- Reports metrics to Statsd via [Stature](https://github.com/nomnom-insights/nomnom.stature)
- Reports exceptions to Rollbar via [Caliban](https://github.com/nomnom-insights/nomnom.caliban)

Only useful if you're using Duckula :wink:


## Options


- `name`: (optional) name of the monitoring stack, usually will be the name of your service, used only for lifecylce logs
- `exclude-pattern`:  (optional) a Regex  to filter out metrics that you don't want to report on, e.g health-check routes, it will also switch logs to DEBUG only level. Note that exceptions thrown will always be tracked and reported!



## Usage

```clojure

(def system
  {;; see Caliban docs for more info
   :exception-tracker (caliban.tracker/create {:token "rollbar-token"
                                               :environment "production"})
   ;; see Stature docs for more info
   :statsd (stature.metrics/create {:host "localhost" :port 8125 :prefix "duckula-test"})
   ;; put it all together
   :monitoring (component/using
                (duckula.monitoring/create {:name "my-api" :exclude-pattern #".+health-check.+"})
                [:statsd :exception-tracker])
   ;; sets up the web request handler
  ;; ring-compatible web server component must be provided by you
  :ring-handler (component/using
                  (duckula.handler/create handler-config)
                  [:monitoring ; defined above
                   :db :publisher :elasticsearch])
   ;; ----
   ;; rest of your dependencies and the component system
   :publisher (bunnicula.publisher/create rmq-config)
   :db (...)
   :elasticsearch (...)
   })




```

## Changelog


- 2021-12-02 - Updated dependencies
- 2020-03-25 - Add `exclude-pattern` options
- 2019-10-21 - Initial Public Offering

## Roadmap

None at the moment

# Authors

<sup>In alphabetical order</sup>

- [Afonso Tsukamoto](https://github.com/AfonsoTsukamoto)
- [Łukasz Korecki](https://github.com/lukaszkorecki)
- [Marketa Adamova](https://github.com/MarketaAdamova)
