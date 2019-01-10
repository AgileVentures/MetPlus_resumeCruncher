#!/bin/sh
image_name=joaopapereira/metplus-cruncher:$BRANCH_NAME-$SEMAPHORE_BUILD_NUMBER

docker build -f Dockerfile.build --tag ${image_name} .

if [ "$BRANCH_NAME" = development ] || [ "$BRANCH_NAME" = master ]; then
  docker push ${image_name}
fi