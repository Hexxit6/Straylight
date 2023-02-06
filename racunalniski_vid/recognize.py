import torch
import torchvision
from torchvision import transforms
from PIL import Image
import sys

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

transform = transforms.Compose([transforms.Resize((224, 224)),
                                transforms.ToTensor(),
                                transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))])

model = torchvision.models.resnet18(pretrained=True)
model.load_state_dict(torch.load(sys.argv[1]))
model = model.to(device)
model.eval()

# Preprocess the input image
input_image = Image.open(sys.argv[2])
input_tensor = transform(input_image).unsqueeze(0)
input_tensor = input_tensor.to(device)

# Get the predicted class
with torch.no_grad():
    output = model(input_tensor)
    _, predicted_class = torch.max(output.data, 1)

# Convert the output to a probability distribution
probs = torch.softmax(output, dim=1)
confidence = probs[0][predicted_class]

# Print output with the confidence factor
print(f'Predicted class: {predicted_class.item()}')

if predicted_class.item() == 0:
    print("It IS NOT polluted")
elif predicted_class.item() == 1:
    print("It IS polluted")

# print(f'Confidence: {confidence.item():.4f}')
