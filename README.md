# Comments Microservice

## GraphQL

```
{
    allComments(video_id: 1, pagination: {offset: 0, limit: 10}) {
        result {
            user_name
            timestamp
            text
        }
        pagination {
            offset
            limit
            total
        }
    }
}
```