package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.entities.Review
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.review_row.view.*
import java.text.SimpleDateFormat

class ReviewViewHolder(row: View){
    var imgReviewerAvatar: CircleImageView
    var ratingUserReview: RatingBar
    var txtReviewComment: TextView
    var txtUserId: TextView
    var txtCreatedAt: TextView

    init {
        imgReviewerAvatar = row.imgReviewerAvatar
        ratingUserReview = row.ratingUserReview
        txtReviewComment = row.txtReviewComment
        txtUserId = row.txtUserId
        txtCreatedAt = row.txtCreatedAt
    }
}

class ReviewAdapter(var context: Context, var lstReviews: List<Review>): BaseAdapter(){

    private val dateFormat: SimpleDateFormat = SimpleDateFormat(context.getString(R.string.dateFormat))

    override fun getCount(): Int {
        return lstReviews.size
    }

    override fun getItem(position: Int): Any {
        return lstReviews.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View?
        var viewHolder: ReviewViewHolder
        if (convertView ==null){
            var inflater: LayoutInflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.review_row,null)
            viewHolder = ReviewViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ReviewViewHolder
        }
        viewHolder.ratingUserReview.rating = lstReviews.get(position).rating.toFloat()
        viewHolder.txtReviewComment.text = lstReviews.get(position).comment
        viewHolder.imgReviewerAvatar.setImageResource(R.drawable.avatar)
        viewHolder.txtUserId.text = lstReviews.get(position).userId
        viewHolder.txtCreatedAt.text = dateFormat.format(lstReviews.get(position).createAt)

        return view
    }
}
