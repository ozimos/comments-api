(ns user
  (:require
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [comment.system :refer [config]]))

(integrant.repl/set-prep! (constantly config))


(defn -main []
  (go))

(comment
  (go)
  (halt)
  (reset))
