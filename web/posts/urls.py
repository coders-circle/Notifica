from django.conf.urls import url, include
from rest_framework.routers import DefaultRouter
from posts import views, rest_views


router = DefaultRouter()
router.register(r'posts', rest_views.PostViewSet)
router.register(r'comments', rest_views.CommentViewSet)
router.register(r'assignments', rest_views.AssignmentViewSet)
router.register(r'submissions', rest_views.SubmissionViewSet)


urlpatterns = [
    url(r'^feed/$', views.FeedView.as_view(), name='feed'),

    url(r'^api/v1/', include(router.urls)),
]
