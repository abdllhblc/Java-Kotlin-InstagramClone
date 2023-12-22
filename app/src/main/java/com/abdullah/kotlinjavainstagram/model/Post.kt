package com.abdullah.kotlinjavainstagram.model

import com.google.firebase.Timestamp
import java.util.Date

data class Post(val email:String,val comment:String,val downloadUrl:String,val time: String){
    // internetten çekilen verilere model oluşturulurken data class kullanmak daha doğru olur.
}