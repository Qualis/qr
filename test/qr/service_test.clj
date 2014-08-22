(ns qr.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [qr.service :as service]
            [qr.http.header :as header]))

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
  "</.{36}>;rel=\"self\";type=\"text/plain\";title=\"GET URL\";method=\"GET\""})

(def ^:const GET_PNG_LINK_HEADER {
  "Link"
  "</.{36}>;rel=\"self\";type=\"image/png\";title=\"GET PNG\";method=\"GET\""})

(defn set-id-from-link-header
  "gets the id from a response link header and sets last-generated-id"
  [response]
  (def last-generated-id (header/get-id-from-link-header response)))

(defn regex-header-matcher
  "Compare 2 header maps (first having regex value)"
  [regex compareTo]
  (doseq [[headerName headerValue] regex]
    (is (re-matches (re-pattern headerValue) (get compareTo headerName)))))

(defn setup
  "add fixture data"
  []
  (println "setup")
  (let [response (response-for service :post "/"
      :body "{\"url\":\"http://www.qual.is/\"}"
      :headers {"Content-Type" "application/json"})]
    (set-id-from-link-header response)))

(defn teardown
  "remove fixture data"
  []
  (println "teardown"))

(defn fixture
  [test-function]
  (println "wrapping setup")
  (setup)
  (test-function)
  (teardown))

(use-fixtures :once fixture)

(deftest top-level-post-test
  (let [response (response-for service :post "/"
      :body "{\"url\":\"http://www.google.com.au/\"}"
      :headers {"Content-Type" "application/json"})]
    (header/get-id-from-link-header response)
    (is (= (:body response) ""))
    (regex-header-matcher
      (conj DEFAULT_HEADER GET_PNG_LINK_HEADER)
      (:headers response))))

(deftest top-level-get-test
  (let [response (response-for service :get (str "/" last-generated-id))]
    (is (=(:body response) "http://www.qual.is/"))
    (regex-header-matcher
      (conj DEFAULT_HEADER GET_PNG_LINK_HEADER)
      (:headers response))))

(deftest top-level-get-accept-text-plain-test
  (let [response (response-for service :get (str "/" last-generated-id)
      :headers {"accept" "text/plain"})]
    (is (=(:body response) "http://www.qual.is/"))
    (regex-header-matcher
      (conj DEFAULT_HEADER GET_PNG_LINK_HEADER)
      (:headers response))))

(deftest top-level-get-accept-image-png-test
  (let [response (response-for service :get (str "/" last-generated-id)
      :headers {"accept" "image/png"})]
    (is (=(:body response) "the png"))
    (regex-header-matcher
      (conj DEFAULT_HEADER GET_URL_LINK_HEADER)
      (:headers response))))

(deftest about-page-test
  (let [response (response-for service :get "/about")]
    (is (.contains (:body response) "Clojure 1.6"))
    (regex-header-matcher
      DEFAULT_HEADER
      (:headers response))))
