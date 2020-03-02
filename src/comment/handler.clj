(ns comment.handler
 (:require 
  [reitit.swagger :as swagger]
  [reitit.ring.middleware.muuntaja :as muuntaja]
  [comment.middleware :as mw]  
  [muuntaja.core :as m]
  [reitit.coercion.spec]
  [reitit.ring.coercion :as coercion]
  [reitit.ring.middleware.exception :as exception]
  [reitit.swagger-ui :as swagger-ui]
  [reitit.ring :as ring]))

(def ok (fn [{:keys [db]}] 
         (println ":db " (. db (getClass)))
         {:status 200 :body "ok"}))

(def routes
 [["/swagger.json" 
   {:get {:handler (swagger/create-swagger-handler)
          :no-doc  true
          :swagger {:info {:title       "Comment System API"
                           :description "an api for comments"}}}}]

  ["/comments" 
   {:swagger {:tags ["comments"]}}

   ["" 
    {:get {:summary "Get all comments"
           :handler ok}
     :post {:summary "Create a new comment"
            :parameters {:body {:name string?
                                :slug string?
                                :text string?
                                :parent-comment-id int?}}
            :responses {200 {:body string?}}
            :handler ok}}]  

   ["/:slug" 
    {:get {:summary "Get comments by slug"
           :handler ok}}]
    
       

   ["/id/:id" 
    {:put {:summary "Update a comment by the morderator"
           :parameters {:path {:id int?}}
           :handler ok}
    
     :delete {:summary "Delete a comment by the morderator"
              :parameters {:path {:id int?}}
              :handler ok}}]]])


(defn create-app [db]
 (ring/ring-handler
  (ring/router routes
   {:data {:middleware  [swagger/swagger-feature
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-request-middleware
                         coercion/coerce-response-middleware
                         mw/db]
           :db db          
           :coercion reitit.coercion.spec/coercion
           :muuntaja m/instance}})
  (ring/routes
   (swagger-ui/create-swagger-ui-handler
    {:path "/"}))))
