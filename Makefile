production.build:
	rm -rf build
	./gradlew bootWar

production.deploy:
	sh backend-deploy.sh

production.rollback:
	ssh nov29 '. ~/.zshrc; sh /Cataria/rollback.sh'

production.build-and-deploy: production.build production.deploy