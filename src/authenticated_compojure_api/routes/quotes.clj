(ns authenticated-compojure-api.routes.quotes
  (:require [authenticated-compojure-api.route-functions.quotes :as response]
            [authenticated-compojure-api.middleware.token-auth :refer [token-auth-mw]]
            [authenticated-compojure-api.middleware.cors :refer [cors-mw]]
            [authenticated-compojure-api.queries.query-defs :as query]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [authenticated-compojure-api.auth-resources.token-auth-backend :refer [token-backend]]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer [ok]]
            [schema.core :as s]))

;; ============================================
;; Schema(s)
;; ============================================
(s/defschema Quote {:id Long :author String :quote String})

;; ============================================
;; Routes
;; ============================================
(defroutes* quote-routes
  (middlewares [cors-mw]
               (context "/api" []

                 (GET* "/quote" []
                       :return  [Quote]
                       :summary "Returns an array of all available quotes."
                       (ok (query/all-quotes)))

                 (GET* "/quote/:id" []
                       :return      Quote
                       :path-params [id :- Long]
                       :summary     "Returns the quote with the specified id"
                       (response/get-specific-quote-response id))

                 (wrap-authentication
                  (POST* "/quote" []
                         :return        Quote
                         :body-params   [author :- String quote-string :- String]
                         :header-params [authorization :- String]
                         :summary       "Create a new quote provided the author and quote strings. Requires valid token"
                         :notes         "Authorization header expects the following format 'Token {token}'"
                         :middlewares   [token-auth-mw]
                         (response/post-new-quote-response author quote-string))
                  token-backend)

                 (wrap-authentication
                  (PUT* "/quote/:id" []
                        :return        Quote
                        :path-params   [id :- Long]
                        :body-params   [{author :- String ""} {quote-string :- String ""}]
                        :header-params [authorization :- String]
                        :summary       "Update some or all fields of a specified quote"
                        :notes         "Authorization header expects the following format 'Token {token}'"
                        :middlewares   [token-auth-mw]
                        (response/update-quote-response id author quote-string))
                  token-backend)

                 (wrap-authentication
                  (DELETE* "/quote/:id" {:as request}
                           :path-params   [id :- Long]
                           :header-params [authorization :- String]
                           :summary       "Deletes the specified quote. Requires token to have Admin auth"
                           :notes         "Authorization header expects the following format 'Token {token}'"
                           :middlewares   [token-auth-mw]
                           (response/delete-quote-response request id))
                  token-backend))))
