# duckula.monitoring

A fully functioning monitoring component for [Duckula](https://github.com/nomnom-insights/nomnom.duckula).

- Reports metrics to Statsd via [Stature](https://github.com/nomnom-insights/nomnom.stature)
- Reports exceptions to Rollbar via [Caliban](https://github.com/nomnom-insights/nomnom.caliban)

Only useful if you're using Duckula :wink:

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
                (duckula.monitoring/create {:name "my-api"})
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

## Roadmap

None at the moment

# Authors

<sup>In alphabetical order</sup>

- [Afonso Tsukamoto](https://github.com/AfonsoTsukamoto)
- [Łukasz Korecki](https://github.com/lukaszkorecki)
- [Marketa Adamova](https://github.com/MarketaAdamova)
