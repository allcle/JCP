from __future__ import unicode_literals

from django.db import models

# 여기부터
from django.contrib.auth.models import User

class Post(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    title = models.CharField(max_length=144)
    subtitle = models.CharField(max_length=144, blank=True)
    content = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)
    def __str__(self):
        return '[{}] {}'.format(self.user.username, self.title)
# 여기까지 restful 실험 코드

class Document(models.Model):
    description = models.CharField(max_length=255, blank=True)
    document = models.FileField(upload_to='documents/')
    uploaded_at = models.DateTimeField(auto_now_add=True)
