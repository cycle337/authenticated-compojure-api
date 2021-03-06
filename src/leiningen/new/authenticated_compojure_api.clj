(ns leiningen.new.authenticated-compojure-api
  (:require [leiningen.core.main :as main]
            [leiningen.new.templates :refer [renderer name-to-path sanitize-ns ->files]]))

(def render (renderer "authenticated-compojure-api"))

(defn authenticated-compojure-api
  [name & opts]
  (let [data {:name      name
              :ns-name   (sanitize-ns name)
              :sanitized (name-to-path name)}]
    (main/info "=====\nBuilding your application...\n")
    (->files data
             ["project.clj"                                                              (render "project.clj"                                                                            data)]
             ["profiles.clj"                                                             (render "profiles.clj"                                                                           data)]
             ["Procfile"                                                                 (render "Procfile"                                                                               data)]
             [".gitignore"                                                               (render ".gitignore"                                                                                 )]
             ["README.md"                                                                (render "README.md"                                                                              data)]
             ["resources/migrations/20171112165826-create-initial-tables.down.sql"       (render "resources/migrations/20171112165826-create-initial-tables.down.sql"                         )]
             ["resources/migrations/20171112165826-create-initial-tables.up.sql"         (render "resources/migrations/20171112165826-create-initial-tables.up.sql"                           )]
             ["resources/sql/user/password_reset_key.sql"                                (render "resources/sql/user/password_reset_key.sql"                                                  )]
             ["resources/sql/user/permission.sql"                                        (render "resources/sql/user/permission.sql"                                                          )]
             ["resources/sql/user/registered_user.sql"                                   (render "resources/sql/user/registered_user.sql"                                                     )]
             ["resources/sql/user/user_permission.sql"                                   (render "resources/sql/user/user_permission.sql"                                                     )]
             ["resources/sql/truncate_all.sql"                                           (render "resources/sql/truncate_all.sql"                                                             )]
             ["script/init_database.sql"                                                 (render "script/init_database.sql"                                                               data)]
             ["src/{{sanitized}}/auth_resources/basic_auth_backend.clj"                  (render "src/authenticated_compojure_api/auth_resources/basic_auth_backend.clj"                  data)]
             ["src/{{sanitized}}/auth_resources/token_auth_backend.clj"                  (render "src/authenticated_compojure_api/auth_resources/token_auth_backend.clj"                  data)]
             ["src/{{sanitized}}/general_functions/user/create_token.clj"                (render "src/authenticated_compojure_api/general_functions/user/create_token.clj"                data)]
             ["src/{{sanitized}}/middleware/authenticated.clj"                           (render "src/authenticated_compojure_api/middleware/authenticated.clj"                           data)]
             ["src/{{sanitized}}/middleware/basic_auth.clj"                              (render "src/authenticated_compojure_api/middleware/basic_auth.clj"                              data)]
             ["src/{{sanitized}}/middleware/cors.clj"                                    (render "src/authenticated_compojure_api/middleware/cors.clj"                                    data)]
             ["src/{{sanitized}}/middleware/token_auth.clj"                              (render "src/authenticated_compojure_api/middleware/token_auth.clj"                              data)]
             ["src/{{sanitized}}/route_functions/auth/get_auth_credentials.clj"          (render "src/authenticated_compojure_api/route_functions/auth/get_auth_credentials.clj"          data)]
             ["src/{{sanitized}}/route_functions/password/password_reset.clj"            (render "src/authenticated_compojure_api/route_functions/password/password_reset.clj"            data)]
             ["src/{{sanitized}}/route_functions/password/request_password_reset.clj"    (render "src/authenticated_compojure_api/route_functions/password/request_password_reset.clj"    data)]
             ["src/{{sanitized}}/route_functions/permission/add_user_permission.clj"     (render "src/authenticated_compojure_api/route_functions/permission/add_user_permission.clj"     data)]
             ["src/{{sanitized}}/route_functions/permission/delete_user_permission.clj"  (render "src/authenticated_compojure_api/route_functions/permission/delete_user_permission.clj"  data)]
             ["src/{{sanitized}}/route_functions/refresh_token/delete_refresh_token.clj" (render "src/authenticated_compojure_api/route_functions/refresh_token/delete_refresh_token.clj" data)]
             ["src/{{sanitized}}/route_functions/refresh_token/gen_new_token.clj"        (render "src/authenticated_compojure_api/route_functions/refresh_token/gen_new_token.clj"        data)]
             ["src/{{sanitized}}/route_functions/user/create_user.clj"                   (render "src/authenticated_compojure_api/route_functions/user/create_user.clj"                   data)]
             ["src/{{sanitized}}/route_functions/user/delete_user.clj"                   (render "src/authenticated_compojure_api/route_functions/user/delete_user.clj"                   data)]
             ["src/{{sanitized}}/route_functions/user/modify_user.clj"                   (render "src/authenticated_compojure_api/route_functions/user/modify_user.clj"                   data)]
             ["src/{{sanitized}}/routes/auth.clj"                                        (render "src/authenticated_compojure_api/routes/auth.clj"                                        data)]
             ["src/{{sanitized}}/routes/password.clj"                                    (render "src/authenticated_compojure_api/routes/password.clj"                                    data)]
             ["src/{{sanitized}}/routes/permission.clj"                                  (render "src/authenticated_compojure_api/routes/permission.clj"                                  data)]
             ["src/{{sanitized}}/routes/preflight.clj"                                   (render "src/authenticated_compojure_api/routes/preflight.clj"                                   data)]
             ["src/{{sanitized}}/routes/refresh_token.clj"                               (render "src/authenticated_compojure_api/routes/refresh_token.clj"                               data)]
             ["src/{{sanitized}}/routes/user.clj"                                        (render "src/authenticated_compojure_api/routes/user.clj"                                        data)]
             ["src/{{sanitized}}/handler.clj"                                            (render "src/authenticated_compojure_api/handler.clj"                                            data)]
             ["src/{{sanitized}}/query_defs.clj"                                         (render "src/authenticated_compojure_api/query_defs.clj"                                         data)]
             ["src/{{sanitized}}/server.clj"                                             (render "src/authenticated_compojure_api/server.clj"                                             data)]
             ["test/{{sanitized}}/auth/credential_retrieval_tests.clj"                   (render "test/authenticated_compojure_api/auth/credential_retrieval_tests.clj"                   data)]
             ["test/{{sanitized}}/password/password_reset_tests.clj"                     (render "test/authenticated_compojure_api/password/password_reset_tests.clj"                     data)]
             ["test/{{sanitized}}/password/request_password_reset_tests.clj"             (render "test/authenticated_compojure_api/password/request_password_reset_tests.clj"             data)]
             ["test/{{sanitized}}/permission/permission_creation_tests.clj"              (render "test/authenticated_compojure_api/permission/permission_creation_tests.clj"              data)]
             ["test/{{sanitized}}/permission/permission_deletion_tests.clj"              (render "test/authenticated_compojure_api/permission/permission_deletion_tests.clj"              data)]
             ["test/{{sanitized}}/preflight_request_options_tests.clj"                   (render "test/authenticated_compojure_api/preflight_request_options_tests.clj"                   data)]
             ["test/{{sanitized}}/refresh_token/refresh_token_deletion_tests.clj"        (render "test/authenticated_compojure_api/refresh_token/refresh_token_deletion_tests.clj"        data)]
             ["test/{{sanitized}}/test_utils.clj"                                        (render "test/authenticated_compojure_api/test_utils.clj"                                        data)]
             ["test/{{sanitized}}/user/user_creation_tests.clj"                          (render "test/authenticated_compojure_api/user/user_creation_tests.clj"                          data)]
             ["test/{{sanitized}}/user/user_deletion_tests.clj"                          (render "test/authenticated_compojure_api/user/user_deletion_tests.clj"                          data)]
             ["test/{{sanitized}}/user/user_modify_tests.clj"                            (render "test/authenticated_compojure_api/user/user_modify_tests.clj"                            data)])
    (main/info "You are good to go!\n=====")))
