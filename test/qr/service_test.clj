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
  "Link"
  "</.*>;rel=\"self\";type=\"text/plain\";title=\"GET URL\";method=\"GET\""})

(def ^:const GET_PNG_LINK_HEADER {
  "Link"
  "</.*>;rel=\"self\";type=\"image/png\";title=\"GET PNG\";method=\"GET\""})

(defn set-id-from-link-header
  "gets the id from a response link header and sets last-generated-id"
  [response]
  (def last-generated-id (second (re-find #"<\/(.*?)>"
    (get (:headers response) "Link")))))

(defn header-matcher
  "Compare 2 header map with first being regex"
  [regex actual]
  (doseq [[headerName headerValue] regex]
    (is (re-matches (re-pattern headerValue) (get actual headerName)))))

(deftest top-level-post-test
  (let [response (response-for service :post "/"
      :params {:url "http://www.google.com.au/"})]
    (set-id-from-link-header response)
    (is (= (:body response) ""))
    (header-matcher
      (conj DEFAULT_HEADER GET_PNG_LINK_HEADER)
      (:headers response))))

(deftest top-level-get-test
  (let [response (response-for service :get "/")]
    (is (=(:body response) "the url"))
    (header-matcher
      (conj DEFAULT_HEADER GET_PNG_LINK_HEADER)
      (:headers response))))

(deftest top-level-get-accept-text-plain-test
  (let [response (response-for service :get "/"
      :headers {"accept" "text/plain"})]
    (is (=(:body response) "the url"))
    (header-matcher
      (conj DEFAULT_HEADER GET_PNG_LINK_HEADER)
      (:headers response))))

(deftest top-level-get-accept-image-png-test
  (let [response (response-for service :get "/"
      :headers {"accept" "image/png"})]
    (is (=(:body response) "the png"))
    (header-matcher
      (conj DEFAULT_HEADER GET_URL_LINK_HEADER)
      (:headers response))))

(deftest about-page-test
  (let [response (response-for service :get "/about")]
    (is (.contains (:body response) "Clojure 1.6"))
    (header-matcher
      DEFAULT_HEADER
      (:headers response))))
