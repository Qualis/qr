(ns qr.service
    (:require [io.pedestal.http :as bootstrap]
              [io.pedestal.http.route :as route]
              [io.pedestal.http.body-params :as body-params]
              [io.pedestal.http.route.definition :refer [defroutes]]
              [ring.util.response :as ring-response]
              [ring.util.request :as ring-request]
              [clojure.data.json :as json]
              [qr.http.header :as header]
              [qr.persistence.riak :as persistence]
              [clj.qrgen :as qr]
              [clojure.java.io :as io]))

(defn get-qr-code
  "returns the qr code for a given request/id"
  [request]
  (qr/as-bytes (qr/from (ring-request/request-url request))))

(defn get-response
  "get response"
  [linkHeader linkHeaderValue contentTypeHeader body]
  (ring-response/content-type (ring-response/header
    (ring-response/response body)
      linkHeader linkHeaderValue) contentTypeHeader))

(defn get-text-plain-response
  "get plain text response"
  [id]
  (let [[linkHeader linkHeaderValue] (header/get-png-link-header id)]
    (get-response
      linkHeader linkHeaderValue "text/plain"
      (persistence/get-destination-by-id id))))

(defn get-image-png-response
  "get image png response"
  [request id]
  (let [[linkHeader linkHeaderValue] (header/get-url-link-header id)]
    (get-response
      linkHeader linkHeaderValue "image/png"
      (io/input-stream (get-qr-code request)))))

(defn about-page
  "Serve about page"
  [request]
  (ring-response/response (format "Clojure %s - served from %s"
                        (clojure-version)
                        (route/url-for ::about-page))))

(defn top-level
  "Serve top-level request"
  [request]
  (let [id (get-in request [:path-params :id])]
      (if (= "text/plain" (get (:headers request) "accept"))
        (get-text-plain-response id)
        (get-image-png-response request id))))

(defn top-level-post
  "Satisfy top-level post request"
  [request]
  (let [[linkHeader linkHeaderValue responseValue]
      (conj (header/get-png-link-header
        (persistence/create-record (get (:json-params request) :url))) "")]
    (ring-response/header
      (ring-response/response responseValue)
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
