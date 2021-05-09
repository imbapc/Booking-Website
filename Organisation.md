- Summary of team works:

  - Jiakai Li:
      - Discussed the domain model and implemented in the code (including some necessary changes to the class and also related annotations)
      - Worked on implementation of the async sub/pub requests 
      - Worked on all different things and test cases in a separate branch (jiakai-li-branch) and fix master bug at the end
  
  
  - Frederick Hy:
      - Worked on implementation of concertresource
  
- Strategy to minimize the chance of concurrency errors
  - In different implementations, we used transactions to separate operations from different users. Especially for `POST` requests
  - When dealing with publish, we used `synchronized (subs)` to lock the `subs` object and prevent from concurrency errors

- Domain model

  - In this project we identified five domain classes

    - CONCERT
    - PERFORMER
    - BOOKING
    - SEAT
    - USER

  - Where `USER` is relatively separated from the other four, meaning that it is mainly used for authentication, the other four entities relationship is displayed as below:

    ![image](./spec/domain-relation.png)

    Some notes about the relationship:

    - `CONCERT` and `PERFORMER` are unidirectional one to many relationship
    - `CONCERT` and `BOOKING` are bidirectional one to many relationship (since we need to know which concert a booking was made for), also with cascading
    - `BOOKING` and `SEAT` are unidirectional one to many relationship (`SEAT` has date information in it)
    - `CONCERT` is related to `PERFORMER` and dates information, we also added the optimization strategy of SUBSELECT to avoid the n + 1 problem
    - For the `USER` entity, we also added the version annotation to make the record versioned as well

