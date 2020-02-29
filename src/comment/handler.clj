(ns comment.handler
 (:require 
  [reitit.core :as r]
  [ring.adapter.jetty :as jetty]
  [reitit.swagger :as swagger]
  [reitit.ring.middleware.muuntaja :as muuntaja]
  [muuntaja.core :as m]
  [reitit.coercion.spec]
  [reitit.ring.coercion :as coercion]
  [reitit.ring.middleware.exception :as exception]
  [reitit.swagger-ui :as swagger-ui]
  [reitit.ring :as ring]))

(def ok (constantly {:status 200 :body "ok"}))
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
           :handler "ok"}
     :post {:summary "Create a new comment"
            :parameters {:body {:name string?
                                :slug string?
                                :text string?
                                :parent-comment-id int?}}
            :responses {200 {:body string?}}
            :handler "ok"}}]  

   ["/:slug" 
    {:get {:summary "Get comments by slug"
           :handler "ok"}}]
    
       

   ["/id/:id" 
    {:put {:summary "Update a comment by the morderator"
           :parameters {:path {:id int?}}
           :handler "ok"}
    
     :delete {:summary "Delete a comment by the morderator"
              :parameters {:path {:id int?}}
              :handler "ok"}}]]])
     
     

(def router 
 (ring/router routes
  {:data {:middleware  [swagger/swagger-feature
                        muuntaja/format-negotiate-middleware
                        muuntaja/format-response-middleware
                        exception/exception-middleware
                        muuntaja/format-request-middleware
                        coercion/coerce-request-middleware
                        coercion/coerce-response-middleware]
          :coercion reitit.coercion.spec/coercion
          :muuntaja m/instance}}))


(def app
 (ring/ring-handler router
  (ring/routes
   (swagger-ui/create-swagger-ui-handler
    {:path "/"}))))

(defn start []
 (jetty/run-jetty #'app {:port 3000 :join false})
 (println "server started on port 3000"))

(comment
 (r/routes router)
 (app {:request-method :get :uri "/ping"})
 (r/match-by-path router "/ping")
 (r/match-by-path router "/comments/id/1")
 (r/match-by-name router ::ping)
 (start))
