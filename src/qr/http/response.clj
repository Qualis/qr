(ns qr.http.response
  (:require [ring.util.response :as ring-response]
            [ring.util.request :as ring-request]
            [clojure.java.io :as io]
            [qr.http.header :as header]))

(defn response-for
  [linkHeader linkHeaderValue contentTypeHeader body]
  (ring-response/content-type (ring-response/header
    (ring-response/response body)
      linkHeader linkHeaderValue) contentTypeHeader))

(defn text-plain-response-for
  [id get-destination-for]
  (let [[linkHeader linkHeaderValue] (header/get-png-link-header id)]
    (response-for
      linkHeader linkHeaderValue "text/plain"
      (get-destination-for id))))

(defn redirect-response-for
  [id get-destination-for]
  (let [[linkHeader linkHeaderValue] (header/get-png-link-header id)]
    (ring-response/header
      (ring-response/redirect (get-destination-for id))
      linkHeader linkHeaderValue)))

(defn image-png-response-for
  [request id qr-generator]
  (let [[linkHeader linkHeaderValue] (header/get-url-link-header id)]
    (response-for
      linkHeader linkHeaderValue "image/png"
      (io/input-stream (qr-generator (ring-request/request-url request))))))
