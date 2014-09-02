(ns qr.persistence.riak
  (:require [clojurewerkz.welle.core :as welle]
            [clojurewerkz.welle.buckets :as buckets]
            [clojurewerkz.welle.kv :as key-store]
            [qr.persistence.id :as id-generator]))

(def ^:const BUCKET "resource")
(def connection (welle/connect-via-pb))

(defn read-record
  [id]
  (key-store/fetch-one connection BUCKET id))

(defn exists?
  [id]
  (key-store/has-value? (read-record id)))

(defn create-record
  [url]
  (let [id (id-generator/generate-id exists?)]
    (key-store/store connection BUCKET id {:destination url}
      {:content-type "application/clojure"})
    id))

(defn delete-record
  [id]
  (key-store/delete connection BUCKET id))

(defn delete-all-record
  []
  (doseq [id (buckets/keys-in connection BUCKET)]
    (delete-record id)))

(defn get-value
  [record]
  (get (get record :result) :value))

(defn get-destination-by-id
  [id]
  (get (get-value (read-record id)) :destination))
