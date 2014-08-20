(ns qr.service
    (:require [io.pedestal.http :as bootstrap]
              [io.pedestal.http.route :as route]
              [io.pedestal.http.body-params :as body-params]
              [io.pedestal.http.route.definition :refer [defroutes]]
              [ring.util.response :as ring-resp]
              [clojure.data.json :as json]
              [qr.http.header :as qualis-header]
              [qr.persistence.riak :as persistence]))

(defn about-page
  "Serve about page"
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn top-level
  "Serve top-level request"
  [request]
  (let [[linkHeader linkHeaderValue responseValue]
    (if (not= "image/png" (get (:headers request) "accept"))
      (conj (qualis-header/get-png-link-header "123") "the url")
      (conj (qualis-header/get-url-link-header "123") "the png"))]
    (ring-resp/header
      (ring-resp/response responseValue)
        linkHeader linkHeaderValue)))

(defn top-level-post
  "Satisfy top-level post request"
  [request]
  (let [[linkHeader linkHeaderValue responseValue]
      (conj (qualis-header/get-png-link-header
        (persistence/create-record (get (:params request) :url))) "")]
    (ring-resp/header
      (ring-resp/response responseValue)
        linkHeader linkHeaderValue)))

(defroutes routes
  [[["/" {:get top-level :post top-level-post}
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/about" {:get about-page}]]]])

(def service "Service definition" {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
