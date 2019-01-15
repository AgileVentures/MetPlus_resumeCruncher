#!/bin/sh
image_name=agileventures/metplus-cruncher:$BRANCH_NAME-$SEMAPHORE_BUILD_NUMBER
image_branch_tag=agileventures/metplus-cruncher:$BRANCH_NAME

docker build -f Dockerfile.build --tag ${image_name} .

if [ "$BRANCH_NAME" = development ] || [ "$BRANCH_NAME" = master ]; then
  docker tag ${image_name} ${image_branch_tag}
  docker push ${image_name}
fi