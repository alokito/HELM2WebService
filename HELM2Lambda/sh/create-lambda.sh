if [ -z "$LAMBDA_NAME" ]; then
  echo "LAMBDA_NAME not set, please run awsenv.sh"
elif aws lambda get-function --function-name $LAMBDA_NAME --profile $AWS_PROFILE; then
  echo "Updating existing lambda $LAMBDA_NAME"
  aws s3 cp ../target/HELM2Lambda-1.0.0-SNAPSHOT.jar s3://$LAMBDA_BUCKET/HELM2Lambda-1.0.0-SNAPSHOT.jar  --profile $AWS_PROFILE
  aws lambda update-function-code --function-name $LAMBDA_NAME --s3-bucket $LAMBDA_BUCKET --s3-key HELM2Lambda-1.0.0-SNAPSHOT.jar --publish --profile $AWS_PROFILE
else
  echo "Creating lambda $LAMBDA_NAME"
  aws s3 cp ../target/HELM2Lambda-1.0.0-SNAPSHOT.jar s3://$LAMBDA_BUCKET/HELM2Lambda-1.0.0-SNAPSHOT.jar  --profile $AWS_PROFILE
  LAMBDA_ROLE_ARN=`aws iam get-role  --role-name $LAMBDA_ROLE --query Role.Arn | tr -d '"'`
  aws lambda create-function --function-name $LAMBDA_NAME --runtime java8 \
    --role $LAMBDA_ROLE_ARN --handler org.helm.lambda.RootHandler::route \
    --memory-size 1536 --timeout 60 \
    --code S3Bucket=$LAMBDA_BUCKET,S3Key=HELM2Lambda-1.0.0-SNAPSHOT.jar --profile $AWS_PROFILE
  aws lambda add-permission --function-name $LAMBDA_NAME --statement-id "SID1_"$LAMBDA_NAME --action lambda:InvokeFunction --principal apigateway.amazonaws.com
fi