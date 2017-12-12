#! /bin/bash
# Deploy only if it's not a pull request
if [ -z "$TRAVIS_PULL_REQUEST" ] || [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  # Deploy only if we're testing the master branch
  if [ "$TRAVIS_BRANCH" == "master" ]; then
    echo "Deploying $TRAVIS_BRANCH on $TASK_DEFINITION"
    pwd
    ls
    #./bin/ecs_deploy.sh -c $CLUSTER_NAME -n $SERVICE -i $REMOTE_IMAGE_URL:$TRAVIS_BRANCH
    echo "aws ecs update-service -region $AWS_DEFAULT_REGION -cluster $CLUSTER_NAME -service $SERVICE_NAME  -task-definition $TASK_DEFINITION"
    eval $(aws ecs update-service --region "${AWS_DEFAULT_REGION}" --cluster "${CLUSTER_NAME}" --service "${SERVICE_NAME}"  --task-definition "${TASK_DEFINITION}")
  else
    echo "Skipping deploy because it's not an allowed branch"
  fi
else
  echo "Skipping deploy because it's a PR"
fi

