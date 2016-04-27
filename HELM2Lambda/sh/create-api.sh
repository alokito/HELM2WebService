MAPPING_TEMPLATE_RAW='#set($allParams = $input.params())
{
"body" : "$util.escapeJavaScript($input.path('$'))",
"operation" : "$context.httpMethod",
"stage" : "$context.stage",
"request_id" : "$context.requestId",
"api_id" : "$context.apiId",
"resource_path" : "$context.resourcePath",
"resource_id" : "$context.resourceId",
"http_method" : "$context.httpMethod",
"source_ip" : "$context.identity.sourceIp",
"user-agent" : "$context.identity.userAgent",
"account_id" : "$context.identity.accountId",
"api_key" : "$context.identity.apiKey",
"caller" : "$context.identity.caller",
"user" : "$context.identity.user",
"user_arn" : "$context.identity.userArn",

"params" : {
#foreach($type in $allParams.keySet())
#set($params = $allParams.get($type))
"$type" : {
#foreach($paramName in $params.keySet())
"$paramName" : "$util.escapeJavaScript($params.get($paramName))"
#if($foreach.hasNext),#end
#end
}
#if($foreach.hasNext),#end
#end
}
}'

if [ -z "$LAMBDA_NAME" ]; then
  echo "LAMBDA_NAME not set, please run awsenv.sh"
else
 LAMBDA_ARN=`aws lambda get-function --function-name $LAMBDA_NAME --profile $AWS_PROFILE --query Configuration.FunctionArn | tr -d '"'`
 if [ -z "$LAMBDA_ARN" ]; then
   echo "could not determine LAMBDA_ARN, please run create-lambda.sh"
 else
  API_ID=`aws apigateway get-rest-apis --query 'items[?name==\`'$API_NAME'\`] | [0].id' --profile $AWS_PROFILE`
  if [ -z "API_ID" ]; then
    echo "api $API_NAME already exists with id $API_ID, please delete if you wish to recreate."
  else
    aws apigateway create-rest-api --name $API_NAME --profile $AWS_PROFILE
    API_ID=`aws apigateway get-rest-apis --query 'items[?name==\`'$API_NAME'\`] | [0].id' --profile $AWS_PROFILE | tr -d '"'`
    PATH0=`aws apigateway get-resources --rest-api-id $API_ID --profile $AWS_PROFILE --query 'items[?path==\`/\`] | [0].id' | tr -d '"'`
    aws apigateway create-resource --rest-api-id $API_ID --parent-id $PATH0 --path-part "{path1}"  --profile $AWS_PROFILE
    PATH1=`aws apigateway get-resources --rest-api-id $API_ID --profile $AWS_PROFILE --query 'items[?path==\`/{path1}\`] | [0].id' | tr -d '"'`
    aws apigateway create-resource --rest-api-id $API_ID --parent-id $PATH1 --path-part "{path2}"  --profile $AWS_PROFILE
    PATH2=`aws apigateway get-resources --rest-api-id $API_ID --profile $AWS_PROFILE --query 'items[?path==\`/{path1}/{path2}\`] | [0].id' | tr -d '"'`
    aws apigateway create-resource --rest-api-id $API_ID --parent-id $PATH2 --path-part "{path3}"  --profile $AWS_PROFILE
    PATH3=`aws apigateway get-resources --rest-api-id $API_ID --profile $AWS_PROFILE --query 'items[?path==\`/{path1}/{path2}/{path3}\`] | [0].id' | tr -d '"'`
    aws apigateway create-resource --rest-api-id $API_ID --parent-id $PATH3 --path-part "{path4}"  --profile $AWS_PROFILE
    PATH4=`aws apigateway get-resources --rest-api-id $API_ID --profile $AWS_PROFILE --query 'items[?path==\`/{path1}/{path2}/{path3}/{path4}\`] | [0].id' | tr -d '"'`

    MAPPING_TEMPLATE=`echo $MAPPING_TEMPLATE_RAW | perl -pe 's/"/\\\\"/g' | tr '\r\n' ' '`
    LAMBDA_REGION=`echo $LAMBDA_ARN | perl -e '$_ =<>; /lambda:([^:]+):/; print $1;'`
    LAMBDA_URI="arn:aws:apigateway:$LAMBDA_REGION"":lambda:path/2015-03-31/functions/$LAMBDA_ARN/invocations"
    for APIPATH in $PATH0 $PATH1 $PATH2 $PATH3 $PATH4; do
      for METHOD in 'GET' 'POST' 'DELETE'; do
        echo adding $METHOD method to path id "$APIPATH";
        aws apigateway put-method --rest-api-id $API_ID --resource-id $APIPATH --http-method $METHOD --authorization-type none --profile $AWS_PROFILE
        aws apigateway put-integration --rest-api-id $API_ID --resource-id $APIPATH --http-method $METHOD --integration-http-method POST --type AWS --request-templates \
          '{"application/json":"'$MAPPING_TEMPLATE'","application/xml":"'$MAPPING_TEMPLATE'"}' --uri $LAMBDA_URI --profile $AWS_PROFILE
        aws apigateway put-method-response --rest-api-id $API_ID --resource-id $APIPATH --http-method $METHOD --status-code 200 --profile $AWS_PROFILE
        aws apigateway put-integration-response --rest-api-id $API_ID --resource-id $APIPATH --http-method $METHOD --status-code 200 \
          --response-templates "{\"application/json\": \"\"}" --profile $AWS_PROFILE
      done
    done
  fi
 fi
fi