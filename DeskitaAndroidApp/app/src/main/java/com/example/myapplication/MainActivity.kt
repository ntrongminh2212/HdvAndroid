package com.example.myapplication

import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.fragment.HomeFragment
import com.example.myapplication.fragment.PersonalFragment
import com.example.myapplication.fragment.TopdeskitaFragment
import kotlinx.android.synthetic.main.main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        setCurrentFragment(HomeFragment())

        bnavMain.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itHomeBotNav -> setCurrentFragment(HomeFragment())
                R.id.itPersonalBotNav ->setCurrentFragment(PersonalFragment())
                R.id.itTopSellerBotNav -> setCurrentFragment(TopdeskitaFragment())
            }
            true
        }
    }

    fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragContainer,fragment)
            commit()
        }
    }
}