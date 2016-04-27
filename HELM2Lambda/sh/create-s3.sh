if [ -z "$LAMBDA_BUCKET" ]; then
 echo "LAMBDA_BUCKET not set, please run awsenv.sh"
else
  if aws s3 ls s3://$LAMBDA_BUCKET  --profile $AWS_PROFILE > /dev/null; then
      echo "bucket $LAMBDA_BUCKET already exists"
  else
      echo "creating bucket $LAMBDA_BUCKET"
      aws s3 mb s3://$LAMBDA_BUCKET  --profile $AWS_PROFILE
  fi
fi
