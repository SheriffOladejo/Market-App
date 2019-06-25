package com.example.newmarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.newmarket.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckNewOrdersFragment extends Fragment {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    public CheckNewOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_new_orders, container, false);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = view.findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        ordersList.setHasFixedSize(true);
        FirebaseRecyclerOptions<AdminOrders> options =
            new FirebaseRecyclerOptions.Builder<AdminOrders>()
                    .setQuery(ordersRef, AdminOrders.class)
                    .build();

        FirebaseRecyclerAdapter<AdminOrders, CheckNewOrdersFragment.AdminOrdersViewHolder> adapter =
            new FirebaseRecyclerAdapter<AdminOrders, CheckNewOrdersFragment.AdminOrdersViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull CheckNewOrdersFragment.AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {
                    holder.userName.setText("Name: " + model.getName());
                    holder.userPhoneNumber.setText("Phone: " + model.getPhone());
                    holder.userTotalPrice.setText("Total Amount: #" + model.getTotal_Amount());
                    holder.userDateTime.setText("Order at: " + model.getDate() + " " + model.getTime());
                    holder.userShippingAddress.setText("Shipping Address: " + model.getAddress());
                    holder.extras.setText("Extras: " + model.getExtras());

                    holder.showOrdersbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        String uID = getRef(position).getKey();
                        Intent intent = new Intent(getActivity(), AdminUserProductsActivity.class);
                        intent.putExtra("uid", uID);
                        startActivity(intent);
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                            "Yes",
                            "No"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Has this product been delivered?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){
                                String uID = getRef(position).getKey();
                                removeOrder(uID);
                            }
                            else{
                                getActivity().finish();
                            }
                            }
                        });
                        builder.show();
                        }
                    });
                }

                @NonNull
                @Override
                public CheckNewOrdersFragment.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_layout, viewGroup, false);
                    return new CheckNewOrdersFragment.AdminOrdersViewHolder(view);
                }
            };
        ordersList.setAdapter(adapter);
        adapter.startListening();
        return view;
    }

    private static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress, extras;
        public Button showOrdersbutton;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrdersbutton = itemView.findViewById(R.id.show_all_products_btn);
            extras = itemView.findViewById(R.id.extras);
        }
    }

    private void removeOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }

}
