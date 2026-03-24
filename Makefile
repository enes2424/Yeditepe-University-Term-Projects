normal:
	@javac -cp lib/gson-2.8.9.jar:lib/vlcj-3.12.1.jar:lib/jna-4.5.2.jar:lib/jna-platform-4.5.2.jar:lib/slf4j-api-1.7.30.jar:src -d class src/P2P.java
	@java -cp lib/gson-2.8.9.jar:lib/vlcj-3.12.1.jar:lib/jna-4.5.2.jar:lib/jna-platform-4.5.2.jar:lib/slf4j-api-1.7.30.jar:lib/slf4j-simple-1.7.30.jar:class P2P

clean:
	@rm -rf class

prepare-xhost:
	@xhost +local:docker

docker: prepare-xhost
	@docker rm peer 2>/dev/null | true
	@docker build -t p2p .
	docker run -e DISPLAY=$(DISPLAY) -v /tmp/.X11-unix:/tmp/.X11-unix -v $(XDG_RUNTIME_DIR)/pulse:$(XDG_RUNTIME_DIR)/pulse -v ./videos:/videos --name peer p2p

docker-compose: prepare-xhost
	@docker-compose down
	@docker build -t p2p .
	@docker-compose up

open-peer1:
	@docker exec -it peer1 bash

open-peer2:
	@docker exec -it peer2 bash

open-peer3:
	@docker exec -it peer3 bash

clean-docker:
	@docker rm peer 2>/dev/null | true
	@docker-compose down

clean-all-docker:
	@docker system prune -a --volumes

clean-all: clean clean-docker clean-all-docker

.PHONY: normal clean prepare-xhost docker docker-compose open-peer1 open-peer2 open-peer3 clean-docker clean-all-docker clean-all
