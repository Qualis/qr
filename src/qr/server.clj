(ns qr.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [qr.service :as service]))

(defonce runnable-service (server/create-server service/service))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> runnable-service
      (merge {:env :dev
              ::server/join? false
              ::server/routes #(deref #'service/routes)
              ::server/allowed-origins {:creds true
				  :allowed-origins (constantly true)}})
      (server/default-interceptors)
      (server/dev-interceptors))
  (server/start runnable-service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (server/start runnable-service))
