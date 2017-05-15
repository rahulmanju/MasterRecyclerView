
# MasterRecyclerView
Modified Recycler View with Helper Adapters to create list with different view type, Sticky Views,Expandable Lists etc,using very simple,scalable and flexible architecure

## What we have moified in recycler view
We added item postion in signature of onCreateViewHolder method and it helped us to create nested adapter architecture 
   #### Default Signature :
               void onCreateViewHolder(ViewGroup parent, int viewType)
   #### Modifed Signature 
               void onCreateViewHolder(ViewGroup parent, int viewType,int position)
 This is the only change we have done in recyler view 
 
 Now lets see the Magic,Download sample from below link
