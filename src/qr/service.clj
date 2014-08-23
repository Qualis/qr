(ns qr.service
    (:require [io.pedestal.http :as bootstrap]
              [io.pedestal.http.route :as route]
              [io.pedestal.http.body-params :as body-params]
              [io.pedestal.http.route.definition :refer [defroutes]]
              [ring.util.response :as ring-resp]
              [clojure.data.json :as json]
              [qr.http.header :as header]
              [qr.persistence.riak :as persistence]))

(defn get-response
  "get response"
  [linkHeader linkHeaderValue contentTypeHeader contentTypeHeaderValue body]
  (ring-resp/header (ring-resp/header
    (ring-resp/response body)
      linkHeader linkHeaderValue) contentTypeHeader contentTypeHeaderValue))

(defn get-plain-text-response
  "get plain text response"
  [id]
  (let [[linkHeader linkHeaderValue] (header/get-png-link-header id)]
    (get-response
      linkHeader linkHeaderValue
      "Content-Type" "text/plain"
      (persistence/get-destination-by-id id))))

(defn get-image-png-response
  "get image png response"
  [id]
  (let [[linkHeader linkHeaderValue] (header/get-url-link-header id)]
    (get-response
      linkHeader linkHeaderValue
      "Content-Type" "image/png"
      "the png")))

(defn about-page
  "Serve about page"
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                        (clojure-version)
                        (route/url-for ::about-page))))

(defn top-level
  "Serve top-level request"
  [request]
  (let [id (get-in request [:path-params :id])]
      (if (not= "image/png" (get (:headers request) "accept"))
        (get-plain-text-response id)
        (get-image-png-response id))))

(defn top-level-post
  "Satisfy top-level post request"
  [request]
  (let [[linkHeader linkHeaderValue responseValue]
      (conj (header/get-png-link-header
        (persistence/create-record (get (:json-params request) :url))) "")]
    (ring-resp/header
      (ring-resp/response responseValue)
        linkHeader linkHeaderValue)))

(defroutes routes
  [[["/" {:post top-level-post}
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/about" {:get about-page}]
     ["/:id" {:get top-level}]]]])

(def service "Service definition" {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
