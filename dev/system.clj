(ns system
 (:require 
  [integrant.core :as ig]
  [comment.handler :refer [create-app]]
  [migrations]
  [ring.adapter.jetty :as jetty]))

(defmethod ig/init-key :adapter/jetty [_ opts]
  (let [handler (atom (delay (:handler opts)))
        options (-> opts (dissoc :handler) (assoc :join? false))]
    {:handler handler
     :server  (jetty/run-jetty (fn [req] (@@handler req)) options)}))

(defmethod ig/halt-key! :adapter/jetty [_ {:keys [server]}]
  (.stop server))

(defmethod ig/suspend-key! :adapter/jetty [_ {:keys [handler]}]
  (reset! handler (promise)))

(defmethod ig/resume-key :adapter/jetty [key opts old-opts old-impl]
  (if (= (dissoc opts :handler) (dissoc old-opts :handler))
    (do (deliver @(:handler old-impl) (:handler opts))
        old-impl)
    (do (ig/halt-key! key old-impl)
        (ig/init-key key opts))))

(defmethod ig/init-key :comment/handler [_ {:keys [db]}]
  (create-app db))

(defmethod ig/init-key :db/dummy [_ _]
  {:no-db true})

(derive :db/dummy :db/sql)

(derive :db/postgresql :db/sql)

(def base-config
 {:adapter/jetty {:port 3000, :handler (ig/ref :comment/handler)}
  :comment/handler {:db (ig/ref :db/sql)}})

(def dummy-db-config
  (assoc base-config :db/dummy {}))

(def postgresql-config
  (assoc base-config :db/postgresql {}))


