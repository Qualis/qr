(ns qr.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [qr.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(def ^:const DEFAULT_HEADER {
  "Content-Type" "text/html;charset=UTF-8"
  "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
  "X-Frame-Options" "DENY"
  "X-Content-Type-Options" "nosniff"
  "X-XSS-Protection" "1; mode=block"})

(def ^:const GET_URL_LINK_HEADER {
  "Link" "</123>; rel=\"self\"; type=\"text/plain\"; title=\"GET URL\"; method=\"GET\""})

(def ^:const GET_PNG_LINK_HEADER {
  "Link" "</123>; rel=\"self\"; type=\"image/png\"; title=\"GET PNG\"; method=\"GET\""})

(deftest top-level-get-test
  (is (=
       (:body (response-for service :get "/"))
       "the url"))
  (is (=
       (:headers (response-for service :get "/"))
       (conj DEFAULT_HEADER GET_URL_LINK_HEADER))))

(deftest top-level-get-accept-text-plain-test
  (is (=
       (:body (response-for service :get "/" :headers {"accept" "text/plain"}))
       "the url"))
  (is (=
       (:headers (response-for service :get "/" :headers {"accept" "text/plain"}))
       (conj DEFAULT_HEADER GET_PNG_LINK_HEADER))))

(deftest top-level-get-accept-image-png-test
  (is (=
       (:body (response-for service :get "/" :headers {"accept" "image/png"}))
       "the png"))
  (is (=
       (:headers (response-for service :get "/" :headers {"accept" "image/png"}))
       (conj DEFAULT_HEADER GET_URL_LINK_HEADER))))

(deftest top-level-post-test
  (is (=
       (:body (response-for service :post "/"
               :params {:url "http://www.google.com.au/"}))
       ""))
  (is (=
       (:headers (response-for service :post "/"
               :params {:url "http://www.google.com.au/"}))
       (conj DEFAULT_HEADER GET_PNG_LINK_HEADER))))

(deftest about-page-test
  (is (.contains
       (:body (response-for service :get "/about"))
       "Clojure 1.6"))
  (is (=
       (:headers (response-for service :get "/about"))
       DEFAULT_HEADER)))
