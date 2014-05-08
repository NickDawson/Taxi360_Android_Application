package sg.edu.astar.ihpc.taxidriver.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;


import sg.edu.astar.ihpc.taxidriver.R;
import sg.edu.astar.ihpc.taxidriver.entity.DriverDestination;
import sg.edu.astar.ihpc.taxidriver.entity.Location;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<DriverDestination> implements View.OnTouchListener{
	private ArrayList<DriverDestination> driverDestination;
	private int layoutResourceId;
	private Context context;
	private Geocoder geocoder;
	
	private List<Address> addresses;
	private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed properties
    private View mView;
    private DismissCallbacks mCallbacks;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private Object mToken;
    private VelocityTracker mVelocityTracker;
    private float mTranslationX;
    private String listtype;

    public interface DismissCallbacks {
        /**
         * Called to determine whether the view can be dismissed.
         */
        boolean canDismiss(Object token);

        /**
         * Called when the user has indicated they she would like to dismiss the view.
         *
         * @param view  The originating {@link View} to be dismissed.
         * @param token The optional token passed to this object's constructor.
         */
        void onDismiss(View view, Object token);
    }
   
	public PlacesAdapter(Context context, int layoutResourceId, ArrayList<DriverDestination> driverDestination,String type) {
		super(context, layoutResourceId, driverDestination);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.driverDestination = driverDestination;
		this.listtype=type;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RequestHolder holder = null;
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		holder = new RequestHolder();
		holder.request = driverDestination.get(position);
		holder.name = (TextView)row.findViewById(R.id.des);
		holder.selectButton=(ImageButton) row.findViewById(R.id.select);
		holder.selectButton.setBackgroundResource(R.drawable.accept);
		if(listtype.equalsIgnoreCase("myplaces"))
			holder.selectButton.setBackgroundResource(R.drawable.delete);
		holder.selectButton.setTag(holder.request);
		
		row.setTag(holder);
		setupItem(holder);
		return row;
	}
private void setupItem(RequestHolder holder) {
		
		JSONObject jsonObject = null;
		String url="http://maps.googleapis.com/maps/api/geocode/json?sensor=false&language=en&latlng=";
		 url=url+Double.toString(holder.request.getLocation().getLatitude())+","+Double.toString(holder.request.getLocation().getLongitude());
					String reversegeocode=Server.getInstance()
							.connect("GET", url).getResponse();
					Log.d("reverse geocode",reversegeocode);
					try {
						jsonObject = new JSONObject(reversegeocode);
						List<String> items = Arrays.asList(jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address").split("\\s*,\\s*"));
						//String[] addr=jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address").split(",");
						holder.name.setText(items.get(0));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
}
	public static class RequestHolder {
		DriverDestination request;
		TextView name;
		ImageButton selectButton;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		
	    
	        // offset because the view is translated during swipe
	        motionEvent.offsetLocation(mTranslationX, 0);

	        if (mViewWidth < 2) {
	            mViewWidth = mView.getWidth();
	        }

	        switch (motionEvent.getActionMasked()) {
	            case MotionEvent.ACTION_DOWN: {
	                // TODO: ensure this is a finger, and set a flag
	                mDownX = motionEvent.getRawX();
	                mDownY = motionEvent.getRawY();
	                if (mCallbacks.canDismiss(mToken)) {
	                    mVelocityTracker = VelocityTracker.obtain();
	                    mVelocityTracker.addMovement(motionEvent);
	                }
	                return false;
	            }

	            case MotionEvent.ACTION_UP: {
	                if (mVelocityTracker == null) {
	                    break;
	                }

	                float deltaX = motionEvent.getRawX() - mDownX;
	                mVelocityTracker.addMovement(motionEvent);
	                mVelocityTracker.computeCurrentVelocity(1000);
	                float velocityX = mVelocityTracker.getXVelocity();
	                float absVelocityX = Math.abs(velocityX);
	                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
	                boolean dismiss = false;
	                boolean dismissRight = false;
	                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
	                    dismiss = true;
	                    dismissRight = deltaX > 0;
	                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
	                        && absVelocityY < absVelocityX
	                        && absVelocityY < absVelocityX && mSwiping) {
	                    // dismiss only if flinging in the same direction as dragging
	                    dismiss = (velocityX < 0) == (deltaX < 0);
	                    dismissRight = mVelocityTracker.getXVelocity() > 0;
	                }
	                if (dismiss) {
	                    // dismiss
	                    mView.animate()
	                            .translationX(dismissRight ? mViewWidth : -mViewWidth)
	                            .alpha(0)
	                            .setDuration(mAnimationTime)
	                            .setListener(new AnimatorListenerAdapter() {
	                                @Override
	                                public void onAnimationEnd(Animator animation) {
	                                    performDismiss();
	                                }
	                            });
	                } else if (mSwiping) {
	                    // cancel
	                    mView.animate()
	                            .translationX(0)
	                            .alpha(1)
	                            .setDuration(mAnimationTime)
	                            .setListener(null);
	                }
	                mVelocityTracker.recycle();
	                mVelocityTracker = null;
	                mTranslationX = 0;
	                mDownX = 0;
	                mDownY = 0;
	                mSwiping = false;
	                break;
	            }

	            case MotionEvent.ACTION_CANCEL: {
	                if (mVelocityTracker == null) {
	                    break;
	                }

	                mView.animate()
	                        .translationX(0)
	                        .alpha(1)
	                        .setDuration(mAnimationTime)
	                        .setListener(null);
	                mVelocityTracker.recycle();
	                mVelocityTracker = null;
	                mTranslationX = 0;
	                mDownX = 0;
	                mDownY = 0;
	                mSwiping = false;
	                break;
	            }

	            case MotionEvent.ACTION_MOVE: {
	                if (mVelocityTracker == null) {
	                    break;
	                }

	                mVelocityTracker.addMovement(motionEvent);
	                float deltaX = motionEvent.getRawX() - mDownX;
	                float deltaY = motionEvent.getRawY() - mDownY;
	                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
	                    mSwiping = true;
	                    mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
	                    mView.getParent().requestDisallowInterceptTouchEvent(true);

	                    // Cancel listview's touch
	                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
	                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
	                            (motionEvent.getActionIndex() <<
	                                    MotionEvent.ACTION_POINTER_INDEX_SHIFT));
	                    mView.onTouchEvent(cancelEvent);
	                    cancelEvent.recycle();
	                }

	                if (mSwiping) {
	                    mTranslationX = deltaX;
	                    mView.setTranslationX(deltaX - mSwipingSlop);
	                    // TODO: use an ease-out interpolator or such
	                    mView.setAlpha(Math.max(0f, Math.min(1f,
	                            1f - 2f * Math.abs(deltaX) / mViewWidth)));
	                    return true;
	                }
	                break;
	            }
	        }
	        return false;
	    }
		private void performDismiss() {
	        // Animate the dismissed view to zero-height and then fire the dismiss callback.
	        // This triggers layout on each animation frame; in the future we may want to do something
	        // smarter and more performant.

	        final ViewGroup.LayoutParams lp = mView.getLayoutParams();
	        final int originalHeight = mView.getHeight();

	        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

	        animator.addListener(new AnimatorListenerAdapter() {
	            @Override
	            public void onAnimationEnd(Animator animation) {
	                mCallbacks.onDismiss(mView, mToken);
	                // Reset view presentation
	                mView.setAlpha(1f);
	                mView.setTranslationX(0);
	                lp.height = originalHeight;
	                mView.setLayoutParams(lp);
	            }
	        });

	        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	            @Override
	            public void onAnimationUpdate(ValueAnimator valueAnimator) {
	                lp.height = (Integer) valueAnimator.getAnimatedValue();
	                mView.setLayoutParams(lp);
	            }
	        });

	        animator.start();
	}
}
