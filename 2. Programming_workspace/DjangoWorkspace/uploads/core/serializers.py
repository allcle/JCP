# rest api 실험 코드

from rest_framework import serializers
from .models import Post
from django.contrib.auth.models import User

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'username', 'email')

class PostSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    class Meta:
        model = Post
        fields = (
            'id',
            'title',
            'subtitle',
            'content',
            'created_at',
        )
        read_only_fields = ('created_at',)