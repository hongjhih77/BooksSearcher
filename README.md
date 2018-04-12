#### Environments:

* Framework: Springboot
* JaxRS implementation: Jersey
* Database: PostgresSQL
* Local: Mac, MiniKube, Docker, ZuluJDK
* Cloud platform: GCP container engine

#### Phase 1: Sample Jersey RESTfull API application lunched up locally

##### Connecting to Cloud SQL: 
   
1. Stage: [Connecting from Kubernetes Engine](https://cloud.google.com/sql/docs/postgres/connect-kubernetes-engine)

2. Local: [Connecting psql Client Using the Cloud SQL Proxy](https://cloud.google.com/sql/docs/postgres/connect-admin-proxy) or Connecting from Kubernetes Engine
    
    Restart the proxy if disconnected. 
    1. Find the port used by poxy and kill the process
        ```sh
        $ [sudo] lsof -i :<PORT>` `kill -9 <PID>
        ```
    2. Restart the proxy 
        ```sh
        $ ./cloud_sql_proxy -instances=<INSTANCE_CONNECTION_NAME>=tcp:5432 \
                          -credential_file=<PATH_TO_KEY_FILE> &
         ```
    
    In my case:
    ```sh
    $ ./cloud_sql_proxy -instances=my-tw-zone-project:asia-east1:postgres-dev=tcp:5432 -credential_file=/Users/jerrylin/Desktop/Projects/credentials/credentials.json &
    ```
##### Package the Java application as a Docker image
```sh
$ ./mvnw -DskipTests package
$ docker build -t hello-java:v1 .
```

##### Deploy your application to Kubernetes

For single app container in a Pod:
```sh
$ kubectl run hello-java \
    --image=hello-java:v1 \
    --port=8080
```

For Kubernetes Deployment manifest file using Cloud SQL proxy
```sh
$ kubectl apply -f deployment.yaml
```

##### Allow external traffic

For single app container in a Pod:
```sh
$ kubectl expose deployment hello-java --type=LoadBalancer
```

For Kubernetes Deployment manifest file using Cloud SQL proxy
```sh
$ kubectl expose deployment myapp --type=LoadBalancer
```

Automatically opens up a browser window
```sh
$ minikube service myapp
```

##### Problem set of Phase 1:

1. The application.properties file will be packaged. If the database configuration want to be set dynamically, redeployment will be needed. 

2. What if the approval of schema creation from DBA needed? 
    
    Tools of version control database:
    * [liquibase](http://www.liquibase.org/) : Automatically generate SQL scripts for DBA code review
    * [flywaydb](https://flywaydb.org/) : check the link of feature comparison between Liquibase, MyBatis and Flywaydb
    
    [How to generate a ddl creation script with a modern Spring Boot + Data JPA and Hibernate setup?](https://stackoverflow.com/questions/36966337/how-to-generate-a-ddl-creation-script-with-a-modern-spring-boot-data-jpa-and-h)
    
#### Cheat Sheet
Open the Kubernetes dashboard in a browser:
```sh
$ minikube dashboard
```

View the Deployment:
```sh
$ kubectl get deployments
```

View the Service you just created:
```sh
kubectl get services
```

View the Pod:
```sh
$ kubectl get pods
```

To see some logs:
```sh
$ kubectl logs <POD-NAME>
```



#### References
* https://codelabs.developers.google.com/codelabs/cloud-springboot-kubernetes/index.html#0
* https://github.com/spring-guides/gs-spring-boot-docker
* https://kubernetes.io/docs/tutorials/stateless-application/hello-minikube/
* https://blog.hasura.io/using-minikube-as-a-docker-machine-to-avoid-sharing-a-local-registry-bf5020b8197