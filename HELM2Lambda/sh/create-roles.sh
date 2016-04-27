if [ -z "$LAMBDA_ROLE" ]; then
 echo "LAMBDA_ROLE not set, please run awsenv.sh"
else
  if aws iam get-role --profile techlab --role-name $LAMBDA_ROLE --query Role.RoleId > /dev/null; then
      echo "role $LAMBDA_ROLE already exists"
  else
      echo "creating role $LAMBDA_ROLE"
      aws iam create-role --role-name $LAMBDA_ROLE --assume-role-policy-document file://lambda_role.json --profile $AWS_PROFILE
  fi
fi


