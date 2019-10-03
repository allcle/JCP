from django.urls import path
from jcp.api_sound import views

urlpatterns = [
    path('uploads/', views.classifySound),
    path('', views.helloworld),
]
