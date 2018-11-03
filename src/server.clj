(ns server
  (:require [coast]
            [routes]
            [components])
  (:gen-class))

(def app (coast/app {:routes routes/routes
                     :storage (coast/env :photos-path)
                     :layout components/layout}))

(defn -main [& [port]]
  (coast/server app {:port port
                     :max-body 100000000}))

(comment
  (-main))
