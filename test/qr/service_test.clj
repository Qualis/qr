(ns qr.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [qr.service :as service]
            [clojure.data.json :as json]
            [qr.http.header :as header]
            [qr.persistence.riak :as persistence]
            [clj.qrgen :as qr]
            [ring.util.codec :as encoder]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(def ^:const DEFAULT_HEADER {
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

(def ^:const TEXT_PLAIN_RESPONSE_HEADER
  (conj header/TEXT_PLAIN_CONTENT_TYPE DEFAULT_HEADER GET_PNG_LINK_HEADER))
(def ^:const IMAGE_PNG_RESPONSE_HEADER
  (conj header/IMAGE_PNG_CONTENT_TYPE DEFAULT_HEADER GET_URL_LINK_HEADER))
(def ^:const TEXT_HTML_RESPONSE_HEADER
  (conj header/TEXT_HTML_CONTENT_TYPE DEFAULT_HEADER))
(def ^:const POST_JSON_RESPONSE_HEADER
  (conj TEXT_HTML_RESPONSE_HEADER GET_PNG_LINK_HEADER))

(def ^:const GET_METHOD_URL "http://www.qual.is/")
(def ^:const POST_METHOD_URL "http://www.google.com.au/")
(def ^:const POST_METHOD_ARGUMENT {:url POST_METHOD_URL})
(def ^:const HOST_URL "http://localhost:8080/")
(def ^:const HOST_HEADER {"Host" "localhost:8080"})

(def ^:const REDIRECT_RESPONSE_HEADER
  (conj DEFAULT_HEADER {"Location" GET_METHOD_URL}))

(defn setup
  "add fixture data"
  []
  (def generated-id (persistence/create-record GET_METHOD_URL)))

(defn fixture
  [test-function]
  (setup)
  (test-function)
  (persistence/delete-record generated-id))

(use-fixtures :once fixture)

(defn regex-header-matcher
  "Compare 2 header maps (first having regex value)"
  [regex compareTo]
  (doseq [[headerName headerValue] regex]
    (is (re-matches (re-pattern headerValue) (get compareTo headerName)))))

(defn get-expected-qr-code
  "returns the qr code image string form for a given ID"
  [id]
  (String. (qr/as-bytes (qr/from (str HOST_URL generated-id)))))

(defn get-url-with-id
  "get url path for last generated id"
  [id]
  (str HOST_URL id))

(deftest top-level-post-test-json
  (let [response (response-for service :post HOST_URL
      :body (json/write-str POST_METHOD_ARGUMENT)
      :headers header/JSON_CONTENT_TYPE)]
    (let [id (header/get-id-from-link-header response)]
      (is (= POST_METHOD_URL (persistence/get-destination-by-id id)))
      (persistence/delete-record id)
      (is (= (:body response) ""))
      (regex-header-matcher POST_JSON_RESPONSE_HEADER (:headers response)))))

(deftest top-level-post-test-form
  (let [response (response-for service :post HOST_URL
      :body (encoder/form-encode POST_METHOD_ARGUMENT)
      :headers (conj header/FORM_CONTENT_TYPE HOST_HEADER))]
    (let [id (header/get-id-from-link-header response)]
      (is (= POST_METHOD_URL (persistence/get-destination-by-id id)))
      (persistence/delete-record id)
      (is (= (:body response) ""))
      (regex-header-matcher
        (conj DEFAULT_HEADER {"Location" (str HOST_URL id)})
        (:headers response)))))

(deftest top-level-get-test
  (let [response (response-for service :get HOST_URL :headers HOST_HEADER)]
    (is (.contains (:body response) "ShortURL - URL shortening service"))
    (regex-header-matcher TEXT_HTML_RESPONSE_HEADER (:headers response))))

(deftest top-level-get-no-accept-header-test
  (let [response (response-for service :get (get-url-with-id generated-id)
      :headers HOST_HEADER)]
    (is (= (:body response) ""))
    (regex-header-matcher REDIRECT_RESPONSE_HEADER (:headers response))))

(deftest top-level-get-accept-text-plain-test
  (let [response (response-for service :get (get-url-with-id generated-id)
      :headers header/ACCEPT_PLAIN_TEXT)]
    (is (=(:body response) GET_METHOD_URL))
    (regex-header-matcher TEXT_PLAIN_RESPONSE_HEADER (:headers response))))

(deftest top-level-get-accept-image-png-test
  (let [response (response-for service :get (get-url-with-id generated-id)
      :headers (conj header/ACCEPT_IMAGE_PNG HOST_HEADER))]
    (is (= (:body response) (get-expected-qr-code generated-id)))
    (regex-header-matcher IMAGE_PNG_RESPONSE_HEADER (:headers response))))

(deftest about-page-test
  (let [response (response-for service :get (str HOST_URL "about"))]
    (is (.contains (:body response) "Clojure 1.6"))
    (regex-header-matcher TEXT_HTML_RESPONSE_HEADER (:headers response))))
