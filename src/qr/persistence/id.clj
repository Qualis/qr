(ns qr.persistence.id
  (:require [hashids.core :as hashid]
    [clj-time.core :as time]
    [clj-time.coerce :as time-coerce]))

(def ^:const SALT "e0a5c4a26051a37e0402b63a7df906a5")

(defn get-timestamp
  "return the timestamp for now"
  []
  (time-coerce/to-long (time/now)))

(defn get-time-id
  "generate timestamp based hashid"
  []
  (let [time-id (hashid/encrypt (get-timestamp) SALT)]
    time-id))

(defn get-rand-id
  "generate random integer based hashid (exclude existing)"
  [exists-checker]
  (loop []
    (let [rand-id (hashid/encrypt (rand-int Integer/MAX_VALUE) SALT)]
      (if (not (exists-checker rand-id))
        rand-id
        (recur)))))

(defn generate-id
  "generate id for record (exclude existing)"
  [exists-checker]
  (let [time-id (get-time-id)]
    (if (not (exists-checker time-id))
      time-id
      (get-rand-id exists-checker))))
