package com.example.mymoviedb

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymoviedb.databinding.FragmentDetailBinding
import com.example.mymoviedb.databinding.FragmentHomeBinding
import com.example.mymoviedb.listmovie.GetPopularMovieResponse
import com.example.mymoviedb.listmovie.ListMovieAdapter
import com.example.mymoviedb.listmovie.Results
import com.example.mymoviedb.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get()= _binding!!
    val sharedPreFile = "login_account"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(sharedPreFile, Context.MODE_PRIVATE)

        binding.rvMovieList.layoutManager = LinearLayoutManager(this.requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.tvWelcomeUser.text = "Welcome, ${sharedPreferences.getString("username", "-")}"
        fetchAllData()

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.ivExit.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            Toast.makeText(requireContext(), "Log Out", Toast.LENGTH_SHORT).show()
        }
//        val spinner: Spinner = binding.ivProfile
//        ArrayAdapter.createFromResource(
//            this.requireActivity(),
//            R.array.profile_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
//        }
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                when( spinner.getPositionForView(p1)){
//                   1 -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
//                }
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                return
//            }
//        }
    }

    private fun fetchAllData(){
        ApiClient.instance.getTrendingMovie()
            .enqueue(object: Callback<GetPopularMovieResponse>{
                override fun onResponse(call: Call<GetPopularMovieResponse>, response: Response<GetPopularMovieResponse>) {
                    val body = response.body()
                    val code = response.code()
                    if (code == 200){
                        showList(body?.results)
                        Log.d("response-code", code.toString())
                    }
                    else{
                        Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                        Log.d("response-code", code.toString())
                    }
                }
                override fun onFailure(call: Call<GetPopularMovieResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showList(data: List<Results>?){
        val adapter = ListMovieAdapter(object : ListMovieAdapter.OnClickListener{
            override fun onClickItem(data: Results) {
                val movieId = data.id
                val clickedToDetail = movieId?.let { HomeFragmentDirections.actionHomeFragmentToDetailFragment(it) }
                clickedToDetail?.let { findNavController().navigate(it) }
            }
        })
        adapter.submitData(data)
        binding.rvMovieList.adapter = adapter
    }
}