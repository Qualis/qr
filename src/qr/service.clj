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
              [clojure.java.io :as io]
              [selmer.parser :as selmer-parser]
              [clj-time.core :as time]
              [clj-time.format :as time-format]))

(def date-time-formatter (time-format/formatters :date))

(defn get-home
  "returns the home page HTML"
  []
  (selmer-parser/render-file "public/home.html" {
    :generated (time-format/unparse date-time-formatter (time/now))}))

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

(defn get-redirect-response
  "get redirect response"
  [id]
  (let [[linkHeader linkHeaderValue] (header/get-png-link-header id)]
    (ring-response/header
      (ring-response/redirect (persistence/get-destination-by-id id))
      linkHeader linkHeaderValue)))

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
  (ring-response/response (get-home)))

(defn top-level-with-id
  "Serve top-level request"
  [request]
  (let [id (get-in request [:path-params :id])]
      (if (= "text/plain" (get (:headers request) "accept"))
        (get-text-plain-response id)
        (if (= "image/png" (get (:headers request) "accept"))
          (get-image-png-response request id)
          (get-redirect-response id)))))

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
  [[["/" {:get top-level}
     ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/about" {:get about-page}]
     ["/:id" {:get top-level-with-id}]
     ["/" {:post top-level-post}]]]])

(def service "Service definition" {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
