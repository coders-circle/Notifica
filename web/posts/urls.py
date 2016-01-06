from django.conf.urls import url, include
from rest_framework.routers import DefaultRouter
from posts import views, rest_views


router = DefaultRouter()
router.register(r'posts', rest_views.PostViewSet, base_name='post')
router.register(r'comments', rest_views.CommentViewSet, base_name='comment')
router.register(r'events', rest_views.EventViewSet, base_name='event')
router.register(r'assignments', rest_views.AssignmentViewSet, base_name='assignment')
router.register(r'submissions', rest_views.SubmissionViewSet, base_name='submission')


urlpatterns = [
    url(r'^$', views.FeedView.as_view(), name='feed'),

    url(r'^api/v1/', include(router.urls)),
]
