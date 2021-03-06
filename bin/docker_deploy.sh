#! /bin/bash
# Deploy only if it's not a pull request
if [ -z "$TRAVIS_PULL_REQUEST" ] || [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  # Deploy only if we're testing the master branch
  if [ "$TRAVIS_BRANCH" == "master" ]; then
    echo "Deploying $TRAVIS_BRANCH on $TASK_DEFINITION"
    pwd
    ls
    echo "$ECS_SERVICE_NAME"
    eval $(aws ecs update-service --region $AWS_DEFAULT_REGION --cluster $ECS_CLUSTER_NAME --service 'test')
    #eval $(aws ecs update-service --region $AWS_DEFAULT_REGION --cluster $ECS_CLUSTER_NAME --service $ECS_SERVICE_NAME --task-definition $ECS_TASK_DEFINITION | sed 's|https://||')
    #echo "aws ecs update-service -region $AWS_DEFAULT_REGION -cluster $ECS_CLUSTER_NAME -service $ECS_SERVICE_NAME  -task-definition $ECS_TASK_DEFINITION"
  else
    echo "Skipping deploy because it's not an allowed branch"
  fi
else
  echo "Skipping deploy because it's a PR"
fi

