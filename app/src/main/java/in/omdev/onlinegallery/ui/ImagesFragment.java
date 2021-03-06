package in.omdev.onlinegallery.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import in.omdev.onlinegallery.R;
import in.omdev.onlinegallery.databinding.FragmentImagesBinding;
import in.omdev.onlinegallery.databinding.ItemImageBinding;
import in.omdev.onlinegallery.databinding.ItemLoaderBinding;
import in.omdev.onlinegallery.model.Image;
import in.omdev.onlinegallery.viewmodel.ImageViewModel;

public class ImagesFragment extends Fragment {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_LOADING = -1;

    private FragmentImagesBinding binding;
    private ImageViewModel viewModel;
    private RequestManager withGlide;
    private StaggeredGridLayoutManager layoutManager;

    public ImagesFragment() {
        // Required empty public constructor
    }

    /**
     * Get new Instance of the images fragment
     *
     * @return A new instance of fragment ImagesFragment.
     */
    public static ImagesFragment newInstance() {
        return new ImagesFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImagesBinding
                .inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }

        withGlide = Glide.with(this);
        viewModel = new ViewModelProvider(getActivity()).get(ImageViewModel.class);
        viewModel.observeAuthLiveData(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser == null) {
                reset();
            }
        });

        // setup recycler view
        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        ImagesAdapter adapter = new ImagesAdapter();
        binding.recyclerView.setAdapter(adapter);

        // Listen for loaded images
        viewModel.getImagesLiveData().observe(getViewLifecycleOwner(), images -> {
            adapter.notifyDataSetChanged();
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return adapter.images.size();
                }

                @Override
                public int getNewListSize() {
                    return images.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(adapter.images.get(oldItemPosition).getId(),
                            viewModel.getImagesLiveData().getValue().get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return true;
                }
            }, false);
            adapter.images.clear();
            adapter.images.addAll(viewModel.getImagesLiveData().getValue());
            result.dispatchUpdatesTo(adapter);
            if (viewModel.state != null) {
                layoutManager.onRestoreInstanceState(viewModel.state);
                viewModel.state = null;
            }
        });

    }

    private void reset() {
        if (viewModel != null) {
            viewModel.getImagesLiveData().reset();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(layoutManager != null) {
            viewModel.state = layoutManager.onSaveInstanceState();
        }
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageHolder> {

        private final ArrayList<Image> images = viewModel.getImages();

        @NonNull
        @Override
        public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_IMAGE) {
                ItemImageBinding imageBinding = ItemImageBinding.inflate(getLayoutInflater(),
                        parent, false);
                return new ImageHolder(imageBinding);
            } else {
                ItemLoaderBinding loaderBinding = ItemLoaderBinding.inflate(getLayoutInflater(),
                        parent, false);
                return new ImageHolder(loaderBinding);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
            if (position < images.size() - 1) {
                holder.setImage(images.get(position));
            }
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ImageHolder holder) {
            super.onViewAttachedToWindow(holder);
            if (holder.image == null) {
                viewModel.getImagesLiveData().loadNextPage();
            }
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == images.size() - 1) {
                return TYPE_LOADING;
            }
            return TYPE_IMAGE;
        }

        private class ImageHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            private final ConstraintSet constraintSet = new ConstraintSet();
            ItemImageBinding binding;
            private Image image;

            public ImageHolder(ItemImageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(this);
            }
            public ImageHolder(ItemLoaderBinding binding) {
                super(binding.getRoot());
            }

            public void setImage(Image image) {
                this.image = image;

                constraintSet.clone(binding.parentConstraint);
                constraintSet.setDimensionRatio(binding.imageItem.getId(),
                        image.getWidth() + ":" + image.getHeight());
                constraintSet.applyTo(binding.parentConstraint);
                withGlide.load(image.getDownload_url())
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .encodeQuality(50)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.imageItem);
            }

            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra("url", image.getDownload_url());
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(getActivity(),
                                    binding.imageItem, "ImageTransition");
                    // start the new activity
                    startActivity(intent, options.toBundle());
                }
            }
        }
    }
}