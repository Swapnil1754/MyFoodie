
const search=()=>{
    console.log("I am Searching...!!!");
    let q = $("#search-input").val();
    let query = q.charAt(0).toUpperCase().concat(q.slice(1));
    
    if(query==''){
        $(".search-result").hide();
    }else{
        console.log(query);
        let url = `http://localhost:9000/api/v2/search/${query}`;
        fetch(url).then((response)=>{
            return response.json();
        }).then((data)=>{
            console.log(data);
            let text = `<div class='list-group'>`;
            data.forEach(element => {
             
                text+=`<a href="/display" class='list-group-item list-group-action' onclick="display(this);"> ${element.restName} </a><br>`
                
                
            });
            text+=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        })
      
    }
    
}

function display(e){
    localStorage.setItem('restName',e.innerHTML);
    console.log("e",e.innerHTML);
    window.location.href="/display"
}

// For PayMent Service


const paymentStart=()=>{
    console.log("Payment Started...!!!");
    let amount = $("#payment_field").val();
    console.log(amount);
    if(amount==''|| amount == null){
        // alert("Amount is Required...!!!");
        swal("Failed...!!!", "Amount is Required...!!!", "error");
        return;
    }
    //ajax to send request to server to generate order
    $.ajax(
        {
            url:'http://localhost:9000/order-services/create_order',
            data:JSON.stringify({amount:amount,info:'order_request'}),
            contentType:'application/json',
            type:'POST',
            dataType:'json',
            success:function(response){
                //if success then this fun will invoked
                console.log(response);
                if(response.status == "created"){
                    let options={
                        key:'rzp_test_57YwJ8uRgM4FPo',
                        amount:response.amount,
                        currency:'INR',
                        name:'Foodie App',
                        description:'Project Donation',
                        image:"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTqMAf_K1Ce--QNlupHZDVCVi79BugZ0Aq6Qg&usqp=CAU",
                        order_id:response.id,
                        handler:function(response){
                            console.log(response.razorpay_payment_id);
                            console.log(response.razorpay_order_id);
                            console.log(response.razorpay_signature);
                            console.log('payment successful !!');
                            // alert("Congrates...!!! Payment Successful...!!!")
                            swal("Congratulations...!!!", "Your Payment is Successful...!!!", "success");
                        },
                        prefill: {
                            name: "",
                            email: "",
                            contact: "",
                        },
                        notes: {
                            address: "Foodie App",
                        },
                        theme: {
                            color: "#3399cc"
                        },
                    };
                
                let rzp=new Razorpay(options);
                rzp.on("payment.failed", function(response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    // alert("Oops Payment Failed...!!!")
                    swal("Failed...!!!", "Oops Payment Failed...!!!", "error");
                });
                rzp.open();
            }
            },
            error:function(error){
                console.log(error);
                alert("Something Went Wrong...!!!")
            },
        }
    );
};