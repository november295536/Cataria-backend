#!/bin/bash

cp build/libs/cataria-* ../api.war
scp ../api.war nov29:/tmp
ssh nov29 -tt '. ~/.zshrc; sh /Cataria/deploy.sh'
rm ../api.war
