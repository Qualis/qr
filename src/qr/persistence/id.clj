(ns qr.persistence.id
  (:require [hashids.core :as hashid]
    [clj-time.core :as time]
    [clj-time.coerce :as time-coerce]))

(def ^:const SALT "e0a5c4a26051a37e0402b63a7df906a5")

(defn get-timestamp
  "return the timestamp for now"
  []
  (time-coerce/to-long (time/now)))

(defn generate-id
  "generate id for record (exclude existing)"
  [exists?]
  (loop []
    (let [time-id (hashid/encrypt (get-timestamp) SALT)]
      (if (not (exists? time-id))
        time-id
        (recur)))))