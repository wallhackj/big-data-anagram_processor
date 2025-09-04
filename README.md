## Local deployment guide
1. Clone repository
```shell
https://github.com/wallhackj/big-data-anagram_processor.git
```
2. `cd` to the root folder of the cloned repository
3. Build jar file
```shell
gradle clean build
```    
5. Run Docker compose stack for local development (Docker have to be installed and running)
```shell
docker-compose up -d
```
5. Run commands inside namenode container:  
```shell
docker exec -it namenode bash
./init/init.sh
```

6. Wait until the initialization script completes.

7. Check the `output` folder in the root directory.  
If it does not exist or is empty, create it and repeat from step 3.

8. The `output` folder contains all results.  
You can modify `input/sample.txt` for new inputs, but **do not change file names or directories**, as this may break `init.sh`.
