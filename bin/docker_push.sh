#! /bin/bash
# Push only if it's not a pull request
if [ -z "$TRAVIS_PULL_REQUEST" ] || [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  # Push only if we're testing the master branch
  if [ "$TRAVIS_BRANCH" == "master" ]; then

    # This is needed to login on AWS and push the image on ECR
    # Change it accordingly to your docker repo
    pip install urllib3[secure]
    pip install --user awscli
    export PATH=$PATH:$HOME/.local/bin
    echo "Pushing2 $AWS_DEFAULT_REGION"
    echo "Pushing3 $IMAGE_NAME"
    eval $(aws ecr get-login --region $AWS_DEFAULT_REGION)
    eval $(aws ecr get-login --region us-east-2)

    # Build and push
    docker build -t $IMAGE_NAME .
    echo "Pushing $IMAGE_NAME:latest"
    docker tag $IMAGE_NAME:latest "$REMOTE_IMAGE_URL:latest"
    docker push "$REMOTE_IMAGE_URL:latest"
    echo "Pushed $IMAGE_NAME:latest"
  else
    echo "Skipping deploy because branch is not 'master'"
  fi
else
  echo "Skipping deploy because it's a pull request"
fi
