(ns qr.persistence.riak
  (:require [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.buckets :as wb]
            [clojurewerkz.welle.kv :as kv]
            [qr.persistence.id :as id-generator]))

(defn create-record
  "create record"
  [url]
  (let [conn (wc/connect) bucket "resource" id (id-generator/generate-id)]
    (wb/update conn bucket)
    (kv/store conn bucket id {:destination url}
      {:content-type "application/clojure"})
      id))

(defn read-record
  "read record"
  [id]
  (let [conn (wc/connect) bucket "resource"]
    (kv/fetch-one conn bucket id)))

(defn delete-record
  "delete record"
  [id]
  (let [conn (wc/connect) bucket "resource"]
    (kv/delete conn bucket id)))

(defn get-value
  "get record value"
  [record]
  (get (get record :result) :value))

(defn get-destination-by-id
  "get destination for given id"
  [id]
  (get (get-value (read-record id)) :destination))
